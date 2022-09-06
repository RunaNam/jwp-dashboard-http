package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_HTML = "/login.html";
    private static final String REGISTER_HTML = "/register.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            Controller controller = RequestMapping.getController(httpRequest);
            HttpResponse httpResponse = controller.service(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}