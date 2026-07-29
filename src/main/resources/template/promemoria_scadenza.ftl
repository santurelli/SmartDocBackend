<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>${oggetto}</title>
    </head>
    <body style="margin:0; padding:0; background-color:#eef2f1; -webkit-text-size-adjust:none;">
        <center>
            <table border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color:#eef2f1;">
                <tr>
                    <td align="center" style="padding:32px 16px;">
                        <table border="0" cellpadding="0" cellspacing="0" width="600" style="max-width:600px; background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 18px rgba(15,60,53,0.08);">

                            <!-- Header -->
                            <tr>
                                <td style="background-color:#0d3c35; padding:28px 32px;">
                                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                        <tr>
                                            <td style="font-family:Arial,Helvetica,sans-serif; font-size:20px; font-weight:bold; color:#ffffff;">
                                                <#if aziendaNome?? && aziendaNome != "">${aziendaNome}<#else>Comunicazione amministrativa</#if>
                                            </td>
                                            <td align="right">
                                                <#if scaduta>
                                                <span style="display:inline-block; background-color:#c62828; color:#ffffff; font-family:Arial,Helvetica,sans-serif; font-size:10px; font-weight:bold; letter-spacing:0.5px; text-transform:uppercase; padding:5px 11px; border-radius:20px;">Scaduta</span>
                                                <#else>
                                                <span style="display:inline-block; background-color:rgba(255,255,255,0.18); color:#ffffff; font-family:Arial,Helvetica,sans-serif; font-size:10px; font-weight:bold; letter-spacing:0.5px; text-transform:uppercase; padding:5px 11px; border-radius:20px;">In scadenza</span>
                                                </#if>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>

                            <!-- Amount card -->
                            <tr>
                                <td style="padding:28px 32px 8px 32px;">
                                    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color:#f0faf9; border:1px solid #cdeae5; border-radius:10px;">
                                        <tr>
                                            <td style="padding:20px 24px;" width="60%">
                                                <div style="font-family:Arial,Helvetica,sans-serif; font-size:10px; font-weight:bold; letter-spacing:1px; text-transform:uppercase; color:#00897b; margin-bottom:6px;">Importo dovuto</div>
                                                <div style="font-family:Arial,Helvetica,sans-serif; font-size:28px; font-weight:bold; color:#0d3c35;">${importo}</div>
                                            </td>
                                            <td style="padding:20px 24px; border-left:1px solid #cdeae5;" width="40%">
                                                <div style="font-family:Arial,Helvetica,sans-serif; font-size:10px; font-weight:bold; letter-spacing:1px; text-transform:uppercase; color:#00897b; margin-bottom:6px;">Scadenza</div>
                                                <div style="font-family:Arial,Helvetica,sans-serif; font-size:16px; font-weight:bold; color:#0d3c35;">${dataScadenza}</div>
                                                <#if numeroFattura?? && numeroFattura != "">
                                                <div style="font-family:'Courier New',monospace; font-size:11px; color:#5a7a75; margin-top:4px;">Fattura ${numeroFattura}</div>
                                                </#if>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>

                            <!-- Message body -->
                            <tr>
                                <td style="padding:24px 32px 32px 32px; font-family:Arial,Helvetica,sans-serif; font-size:14px; line-height:1.6; color:#37474f; white-space:pre-wrap;">${corpo}</td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="padding:18px 32px; border-top:1px solid #eef2f1; font-family:Arial,Helvetica,sans-serif; font-size:11px; color:#9aa9a6; text-align:center;">
                                    Comunicazione automatica<#if aziendaNome?? && aziendaNome != ""> inviata da ${aziendaNome}</#if> &middot; ${currentYear}
                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>
        </center>
    </body>
</html>
