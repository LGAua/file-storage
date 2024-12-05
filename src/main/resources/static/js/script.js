let fileUploadForm = document.getElementById("fileUploadForm")
let fileInput = document.getElementById("uploadFilePanel")

fileUploadForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    let file = fileInput.files[0];

    if (!file) {
        alert("Please select a file")
    }

    const url = "/file"
    const headers = new Headers({
        "Content-Length": file.size(),
        "Expect": "100-Continue"
    });

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: headers
        });

        if (response.status === 100) {
            let uploadResponse = await fetch(url, {
                method: "POST",
                body: file
            });

            let resultMessage = document.getElementById("uploadStatusPanel");
            if (uploadResponse.ok) {
                resultMessage.innerText = "File uploaded successfully";
            } else {
                resultMessage.innerText = `File upload failed: ${uploadResponse.status}`
            }
        } else if (response.status === 417) {
            document.getElementById("uploadStatusPanel").innerText = "Server rejected the request. File size too big (>500mb)";
        } else {
            document.getElementById("uploadStatusPanel").innerText = "Oops, something went wrong";
        }
    } catch (error) {
        let resultMessage = document.getElementById("uploadStatusPanel").innerText = `Error: ${error.message}`;
    }
})