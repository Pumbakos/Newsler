/**
 * Request authentication is performed using a <a href="https://jwt.io/">Json Web Token (JWT)</a> signed with a 384-bit HMAC key,
 * KeyID, three claims and a token expiry time of one hour.
 * <p>
 * To generate token POST request should be sent to '/v1/api/jwt' with body <br/>
 * <pre>
 * {@code {
 *     "email": "encrypted email",
 *     "password": "encrypted password"
 * }}
 * </pre>
 * <p>
 * API responses:
 * <p><b>- 200 - OK</b> when user's credential are valid and token generation was successful</p>
 * <p><b>- 401 - UNAUTHORIZED</b> when user's credential are invalid or token generation was unsuccessful (i.e. wrong decryption key)</p>
 * <br/>
 * All beans registration is done via {@link pl.newsler.auth.JWTConfiguration} to align with Ports and Adapters concept
 * <br/>
 *
 * @author Pumbakos
 * @see com.auth0.jwt.algorithms.Algorithm#HMAC384(byte[])
 * @see pl.newsler.auth.JWTClaim
 * @since 0.0.1
 */
package pl.newsler.auth;