# File Storage Deployment Instructions

Follow these steps to deploy the File Storage application.

## Prerequisites
Ensure you have the following installed:
- Docker
- Docker Compose

## Deployment Steps

### 1. Create the `.env` File
Create a `.env` file in the root directory and add the following fields:
```env
DB_USERNAME=postgres
DB_PASSWORD=postgres
MINIO_USERNAME=filestorage
MINIO_PASSWORD=filestorage
```

### 2. Set Up Prometheus Password
Create a file named `prometheus_password_txt` in the same directory and write your desired password into this file. For example:
```plaintext
mysecurepassword
```

### 3. Start the Application
Run the following command in the directory containing the `docker-compose.yml` file:
```bash
docker-compose up
```

### 4. Verify Deployment
Once the containers are up, access the application and confirm all services are running as expected. Use `docker ps` to check the status of your containers.

---

Enjoy using File Storage!
