package pl.newsler.components.emaillabs;

/**
 * <a href="https://dev.emaillabs.io/#api-Send-sendmail_templates">EmailLabs: Send e-mail with template API documentation</a>
 * <p><strong>Note:</strong> parameters are required unless stated otherwise</p>
 */
public final class ELATemplateParam extends ELAParam {
    /**
     * An array of variables for substitution in a template.<br>
     * <strong>Optional parameter</strong>
     */
    public static String TO_VARS = "to[vars]";

    /**
     * Global vars of template. Works like "vars" in the "to" field. In the case of conflicts, global_vars has priority.
     */
    public static String GLOBAL_VARS = "global_vars";

    /**
     * Template messages Id.<br>
     * <strong>Optional parameter</strong>
     */
    public static String TEMPLATE_ID = "template_id";
}