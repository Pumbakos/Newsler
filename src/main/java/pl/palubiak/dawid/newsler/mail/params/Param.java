package pl.palubiak.dawid.newsler.mail.params;

/**
 * <a href="https://dev.emaillabs.io/#api-Send">EmailLabs: Send e-mails API documentation</a>'
 * <p><strong>Note:</strong> parameters are required unless stated otherwise</p>
 */
public interface Param {
    /**
     * E-mail address of the recipient (as an array). Max 200 addresses.
     */
    String TO_ADDRESS = "to[address]";

    /**
     * Unique parameter for message. Should be similar to email address. F.e. qwerty1234567890@qw.qw
     */
    String TO_MESSAGE_ID = "to[message_id]";

    /**
     * E-mail address of the recipient (as an array). Max 200 addresses. <br/>
     * <strong>Optional parameter.</strong>
     */
    String SMTP_ACCOUNT = "smtp_account";

    /**
     * Subject of the message (max. 128 characters).
     */
    String SUBJECT = "subject";

    /**
     * HTML Message in HTML format.
     */
    String HTML = "html";

    /**
     * Text Message in plain text format.
     */
    String TEXT = "text";

    /**
     * E-mail address of the sender.
     */
    String FROM = "from";

    /**
     * To set your own return-path contact with CSO on email bok@emaillabs.pl.<br/>
     * <strong>Optional parameter.</strong>
     */
    String RETURN_PATH = "return_path";

    /**
     * Available send many emails to one receiver. Set as '1'. <br/>
     * <strong>Optional parameter.</strong>
     */
    String NEW_STRUCTURE = "new_structure";

    /**
     * The displayed name of the sender (max. 128 characters). <br/>
     * <strong>Optional parameter.</strong>
     */
    String FROM_NAME = "from_name";

    /**
     * Additional headers in an array as key => value. <br/>
     * <strong>Optional parameter.</strong>
     *
     * @see java.util.Map
     */
    String headers = "headers";

    /**
     * E-mail address to which a copy of the message will be sent or in case when you want to send many
     * copies set this filed as array.<br/>
     * <strong>Optional parameter.</strong>
     */
    String CC = "cc";

    /**
     * Name of the recipient (max. 128 characters).<br/>
     * <strong>Optional parameter.</strong>
     */
    String CC_NAME = "cc_name";

    /**
     * Set this field as '1' if you want to send many copies.<br/>
     * <strong>Optional parameter.</strong>
     */
    String MULTI_CC = "multi_cc";

    /**
     * E-mail address to which a copy of the message will be sent
     * or in case when you want to send many copies set this filed as array (hidden address).<br/>
     * <strong>Optional parameter.</strong>
     */
    String BCC = "bcc";

    /**
     * Name of the recipient (max. 128 characters).<br/>
     * <strong>Optional parameter.</strong>
     */
    String BCC_NAME = "bcc_name";

    /**
     * Set this field as '1' if you want to send many copies.<br/>
     * <strong>Optional parameter.</strong>
     */
    String MULTI_BCC = "multi_bcc";

    /**
     * E-mail address of "reply to".<br/>
     * <strong>Optional parameter.</strong>
     */
    String REPLY_TO = "reply_to";

    /**
     * Tags of messages in the array (together max. 128 characters). <br/>
     * <strong>Optional parameter.</strong>
     */
    String TAGS = "tags";

    /**
     * Files that you want to add to message.<br/>
     * <strong>Optional parameter.</strong>
     */
    String FILES = "files";

    /**
     * File name. (Also used in inline option or attachment name) <br/>
     * <strong>Optional parameter.</strong>
     */
    String FILES_NAME = "files[name]";

    /**
     * Type of file<br/>
     * <strong>Optional parameter.</strong>
     */
    String FILES_MIME = "files[mime]";

    /**
     * File content in base 64 <br/>
     * <strong>Optional parameter.</strong>
     */
    String FILES_CONTENT = "files[content]";

    /**
     * Set 1 - attachment can be inside HTML
     * (Use this code to set attachment in HTML &quot;&lt;img src="cid:name" /></code&gt;&quot; ).<br/>
     * <strong>Optional parameter.</strong>
     */
    String FILES_INLINE = "files[inline]";
}
