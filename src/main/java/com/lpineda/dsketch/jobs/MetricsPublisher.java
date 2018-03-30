package com.lpineda.dsketch.jobs;

import io.prometheus.client.Gauge;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MetricsPublisher {
    static class ExampleServlet extends HttpServlet {
        static final Gauge requests = Gauge.build()
                .name("processing_speed")
                .help("Number of processed events per second served.").register();

        @Override
        protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
                throws ServletException, IOException {
            resp.getWriter().println("Hello World!");
            // Increment the number of requests.
            requests.inc();
        }
    }

}
