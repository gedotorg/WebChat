package filter;



import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CORSFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request= (HttpServletRequest) servletRequest;

        // Разрешаем доступ с любого источника
        response.setHeader("Access-Control-Allow-Origin", "*");
        // Разрешаем отправку запросов с определенными методами (например, POST)
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        // Разрешаем установку кастомных заголовков
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, Sec-WebSocket-Version, Sec-WebSocket-Key");

        // Продолжаем цепочку фильтров
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
