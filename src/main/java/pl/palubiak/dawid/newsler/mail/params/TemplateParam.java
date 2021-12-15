package pl.palubiak.dawid.newsler.mail.params;

/**
 * <a href="https://dev.emaillabs.io/#api-Send-sendmail_templates">EmailLabs: Send e-mail with template API documentation</a>
 * <p><strong>Note:</strong> parameters are required unless stated otherwise</p>
 */
public interface TemplateParam extends Param{
    /**
     * An array of variables for substitution in a template.<br>
     * <strong>Optional parameter</strong>
     */
    String TO_VARS = "to[vars]";

    /**
     * Global vars of template. Works like "vars" in the "to" field. In the case of conflicts, global_vars has priority.
     */
    String GLOBAL_VARS = "global_vars";

    /**
     * Template messages Id.<br>
     * <strong>Optional parameter</strong>
     */
    String TEMPLATE_ID = "template_id";
}
