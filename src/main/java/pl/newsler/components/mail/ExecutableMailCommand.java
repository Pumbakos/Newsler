package pl.newsler.components.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import pl.newsler.components.user.NLUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor(staticName = "of")
class ExecutableMailCommand implements Runnable {
    private final NLUser user;
    private final MailDetails details;

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        final HashMap<String, String> params = new HashMap<>();
        final StringBuilder query = new StringBuilder();
        String userPass = user.getAppKey() + ":" + user.getSecretKey();
        String auth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

        try {
            //FIXME: It is recommended to use new_sendmail method
            URL url = new URL("https://api.emaillabs.net.pl/api/new_sendmail");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", auth);
            connection.setDoOutput(true);

            String userEmail = user.getEmail().getValue();
            params.put(Param.FROM, userEmail);
            String name = String.format("%s %s", user.getFirstName(), user.getLastName());
            params.put(Param.FROM_NAME, name);
            params.put(Param.SMTP_ACCOUNT, user.getSmtpAccount().getValue());
            params.put(String.format(Param.TO_ADDRESS_NAME, userEmail, name), createKeyValueArray(details));
            params.put(Param.SUBJECT, details.getSubject());
            params.put(Param.HTML, String.format("<pre>%s</pre>", details.getMessage()));
            params.put(Param.TEXT, details.getMessage());

            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    query.append("&");
                }
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            // send data
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(query.toString());
            out.close();

            log.info(String.format("QUERY: %s%n", query));

            // read output
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            log.info(in.readLine());

            in.close();
            connection.disconnect();
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private String createKeyValueArray(MailDetails details) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        details.getToAddresses().forEach((k, v) -> builder.append(k).append(",").append(v));
        builder.append("]");
        return builder.toString();
    }
}
