package com.Mr.fix.it.Util;

public class EmailTemplate
{
    public static String VerificationOrResetTemplate(
        String header,String starting, String ending,
        String buttonText, String link,String token
    )
    {
        String TEMPLATE = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1">
                        <style>
                            body {
                                font-family: Arial, Helvetica, sans-serif;
                                margin: 0;
                                padding: 0;
                                background-color: #f4f4f4;
                            }
                                
                            .container {
                                max-width: 600px;
                                margin: 0 auto;
                                padding: 20px;
                                background-color: #ffffff;
                                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                            }
                                
                            h1 {
                                color: #0C9869;
                            }
                                
                            p {
                                font-size: 16px;
                                line-height: 1.6;
                                color: #333;
                            }
                                
                            a{
                                text-decoration: none;
                            }
                                
                            .button {
                                display: inline-block;
                                padding: 10px 20px;
                                background-color: #0C9869;
                                color: #FFFFFF;
                                border: none;
                                border-radius: 5px;
                                text-align: center;
                                font-size: 16px;
                                margin-top: 5px;
                            }
                            
                            div{
                                color: #FFFFFF;
                            }
                                
                            @media only screen and (max-width: 600px) {
                                .container {
                                    width: 100%%;
                                    padding: 10px;
                                }
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>%s</h1>
                            <p>%s</p>
                            <a href=%s?token=%s class="button"><div>%s</div></a>
                            <p>%s</p>
                        </div>
                    </body>
                    </html>
                """;

        return String.format(TEMPLATE, header, starting, link, token, buttonText, ending);
    }
}