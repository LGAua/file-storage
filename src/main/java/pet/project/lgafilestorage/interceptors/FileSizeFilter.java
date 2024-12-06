//package pet.project.lgafilestorage.interceptors;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class FileSizeFilter implements Filter {
//
//    private static final int MAX_FILE_SIZE = 700 * 1024 * 1024;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
//            throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        if (request.getContentLength() > MAX_FILE_SIZE) {
//            // Устанавливаем статус ошибки
//            response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE); // 413
//            // Устанавливаем содержимое ответа
//            response.setContentType("text/html; charset=UTF-8");
//            response.getWriter().write("""
//                <html>
//                    <body>
//                        <h1>Ошибка: Превышен допустимый размер файла!</h1>
//                        <p>Файл не должен превышать 700 МБ.</p>
//                    </body>
//                </html>
//            """);
//            return; // Завершаем цепочку фильтров
//        }
//
//        // Продолжаем обработку, если размер файла в пределах допустимого
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//}
