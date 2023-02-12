package pl.newsler.components.emaillabs;

/**
 * <a href="https://dev.emaillabs.io/#api-Send-new_sendmail">EmailLabs: Send e-mails API documentation</a>'
 * <p><strong>Note:</strong> parameters are required unless stated otherwise</p>
 */
public sealed class ELAParam permits ELATemplateParam {
    /**
     * An array with a structure in which the keys are email addresses
     * and the values are arrays containing information such as message uuid and recipient name.
     * (Max 200 addresses) The recipient name is optional.
     */
    public static String TO_ADDRESS_NAME = "to[%s][%s]";

    /**
     * Unique parameter for message. Should be similar to email address. F.e. qwerty1234567890@qw.qw
     */
    public static String TO_MESSAGE_ID = "to[message_id]";

    /**
     * E-mail address of the recipient (as an array). Max 200 addresses. <br/>
     */
    public static String SMTP_ACCOUNT = "smtp_account";

    /**
     * Subject of the message (max. 128 characters).
     */
    public static String SUBJECT = "subject";

    /**
     * HTML Message in HTML format.
     */
    public static String HTML = "html";

    /**
     * Text Message in plain text format.
     */
    public static String TEXT = "text";

    /**
     * E-mail address of the sender.
     */
    public static String FROM = "from";

    /**
     * To set your own return-path contact with CSO on email bok@emaillabs.pl.<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String RETURN_PATH = "return_path";

    /**
     * Available send many emails to one receiver. Set as '1'. <br/>
     * <strong>Optional parameter.</strong>
     */
    public static String NEW_STRUCTURE = "new_structure";

    /**
     * The displayed name of the sender (max. 128 characters). <br/>
     * <strong>Optional parameter.</strong>
     */
    public static String FROM_NAME = "from_name";

    /**
     * Additional headers in an array as key => value. <br/>
     * <strong>Optional parameter.</strong>
     *
     * @see java.util.Map
     */
    public static String headers = "headers";

    /**
     * E-mail address to which a copy of the message will be sent or in case when you want to send many
     * copies set this filed as array.<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String CC = "cc";

    /**
     * Name of the recipient (max. 128 characters).<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String CC_NAME = "cc_name";

    /**
     * Set this field as '1' if you want to send many copies.<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String MULTI_CC = "multi_cc";

    /**
     * E-mail address to which a copy of the message will be sent
     * or in case when you want to send many copies set this filed as array (hidden address).<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String BCC = "bcc";

    /**
     * Name of the recipient (max. 128 characters).<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String BCC_NAME = "bcc_name";

    /**
     * Set this field as '1' if you want to send many copies.<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String MULTI_BCC = "multi_bcc";

    /**
     * E-mail address of "reply to".<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String REPLY_TO = "reply_to";

    /**
     * Tags of messages in the array (together max. 128 characters). <br/>
     * <strong>Optional parameter.</strong>
     */
    public static String TAGS = "tags";

    /**
     * Files that you want to add to message.<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String FILES = "files";

    /**
     * File name. (Also used in inline option or attachment name) <br/>
     * <strong>Optional parameter.</strong>
     */
    public static String FILES_NAME = "files[name]";

    /**
     * Type of file<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String FILES_MIME = "files[mime]";

    /**
     * File content in base 64 <br/>
     * <strong>Optional parameter.</strong>
     */
    public static String FILES_CONTENT = "files[content]";

    /**
     * Set 1 - attachment can be inside HTML
     * (Use this code to set attachment in HTML &quot;&lt;img src="cid:name" /></code&gt;&quot; ).<br/>
     * <strong>Optional parameter.</strong>
     */
    public static String FILES_INLINE = "files[inline]";
}
