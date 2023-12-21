package app.httpfilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface HttpFilter extends Filter {

  @Override
  default void init(FilterConfig filterConfig) throws ServletException {
  }

  private boolean isHttp(ServletRequest request, ServletResponse response) {
    return
      request instanceof HttpServletRequest
        && response instanceof HttpServletResponse;
  }

  // HttpServletRequest => Boolean
  boolean myCheckLogic(HttpServletRequest request);

  // HttpServletRequest => HttpServletResponse
  void behaviorIfLogicNotPassed(HttpServletRequest request,
                                        HttpServletResponse response) throws IOException;

  @Override
  default void doFilter(ServletRequest rq0,
                       ServletResponse rs0,
                       FilterChain chain) throws IOException, ServletException {

    // not mine, go on...
    if (!isHttp(rq0, rs0)) chain.doFilter(rq0, rs0);
    else {
      // mine ...
      HttpServletRequest request = (HttpServletRequest) rq0;
      HttpServletResponse response = (HttpServletResponse) rs0;
      if (myCheckLogic(request)) {
        chain.doFilter(request, response);
      } else {
        behaviorIfLogicNotPassed(request, response);
      }
    }
  }

  @Override
  default void destroy() {
  }
}
