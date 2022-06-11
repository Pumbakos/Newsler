package pl.palubiak.dawid.newsler.mail.sender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static pl.palubiak.dawid.newsler.mail.params.Param.*;

public class SendMail {
    public static void main(String[] args) {
        try {
            // code from documentation
            // setup variables
            String appKey = "";
            String secretKey = "";

            String userPass = appKey + ":" + secretKey;
            String basicAuth = "Basic "
                    + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

            // set params
            HashMap<String, String> params = new HashMap<>();
            params.put(SMTP_ACCOUNT, "1.pumbakos.smtp");
            params.put(SUBJECT, "Test message from java source code");
            params.put(HTML, "<p>Newsletter message example</p>");
            params.put(TEXT, "Newsletter message example");
            params.put(FROM, "info@newsler.pl".strip());
            params.put(FROM_NAME, "Info Newsler".strip());
            params.put(TO_ADDRESS, "dawid.palubiak@gmail.com".strip());

            // build query
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    query.append("&");
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                query.append("=");
                query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            // setup connection
            URL url = new URL("https://api.emaillabs.net.pl/api/sendmail"); //FIXME: It is recommended to use new_sendmail method

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setDoOutput(true);

            // send data
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(query.toString());
            out.close();

            // read output
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.print(in.readLine());

        } catch (Exception e) {
            e.printStackTrace();
            e.fillInStackTrace();
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable throwable : suppressed) {
                System.out.println(throwable.getMessage());
            }
        }
    }
}