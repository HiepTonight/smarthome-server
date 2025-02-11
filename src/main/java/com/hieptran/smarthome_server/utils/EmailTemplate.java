//package com.hieptran.smarthome_server.utils;
//
//public class EmailTemplate {
//
//    public static String getVerificationEmail(
//            String recipientName,
//            String verificationCode,
//            int expirationMinutes,
//            String verificationUrl,
//            String supportUrl,
//            String companyAddress,
//            String settingsUrl
//    ) {
//        return String.format("""
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <meta charset="utf-8">
//                <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                <title>Email Verification | SmartHome</title>
//                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
//            </head>
//            <body style="margin: 0; padding: 0; font-family: 'Inter', sans-serif; background-color: #f1f5f9;">
//                <div style="max-width: 640px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); overflow: hidden;">
//                    <!-- Header -->
//                    <div style="background: linear-gradient(135deg, #1e6ae6, #327af0); padding: 40px 0; text-align: center;">
//                        <div style="width: 80px; height: 80px; background: rgba(255, 255, 255, 0.1); border-radius: 50%%; padding: 16px; margin: 0 auto 24px;">
//                            <svg xmlns="http://www.w3.org/2000/svg" width="90%%" height="90%%" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-house-plug">
//                                <path d="M10 12V8.964"/>
//                                <path d="M14 12V8.964"/>
//                                <path d="M15 12a1 1 0 0 1 1 1v2a2 2 0 0 1-2 2h-4a2 2 0 0 1-2-2v-2a1 1 0 0 1 1-1z"/>
//                                <path d="M8.5 21H5a2 2 0 0 1-2-2v-9a2 2 0 0 1 .709-1.528l7-5.999a2 2 0 0 1 2.582 0l7 5.999A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2h-5a2 2 0 0 1-2-2v-2"/>
//                            </svg>
//                        </div>
//                        <h1 style="color: white; font-size: 28px; font-weight: 700; margin: 0;">Verify Your Email</h1>
//                    </div>
//
//                    <!-- Content -->
//                    <div style="padding: 40px; color: #374151;">
//                        <p style="margin-bottom: 24px;">Hi <span style="color: #327af0; font-weight: 600;">%s</span>,</p>
//                        <p style="margin-bottom: 24px;">Thank you for joining HomePod! To activate your account, please use the following verification code:</p>
//
//                        <!-- Verification Code Box -->
//                        <div style="background: #f8fafc; border-radius: 12px; padding: 32px; margin: 32px 0; text-align: center;">
//                            <p style="margin-bottom: 16px;">Your verification code:</p>
//                            <div style="font-size: 40px; font-weight: 700; letter-spacing: 4px; color: #327af0; margin: 16px 0;">%s</div>
//                            <p style="font-size: 14px; color: #6b7280; line-height: 1.6;">Expires in <span style="color: #327af0; font-weight: 600;">%d</span> minutes</p>
//                        </div>
//
//                        <!-- Action Button -->
//                        <p style="text-align: center; margin: 40px 0;">
//                            <a href="%s" style="display: inline-block; padding: 16px 40px; background: linear-gradient(135deg, #1e6ae6, #327af0); color: white; text-decoration: none; border-radius: 8px; font-weight: 600; transition: transform 0.2s, box-shadow 0.2s; box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.1);">Verify Email Now</a>
//                        </p>
//
//                        <!-- Additional Info -->
//                        <p style="font-size: 14px; color: #6b7280; line-height: 1.6; margin-bottom: 24px;">
//                            If you didn't create this account, you can safely ignore this email.
//                            For any questions, visit our <a href="%s" style="color: #1e6ae6; text-decoration: none;">support center</a>.
//                        </p>
//                    </div>
//
//                    <!-- Footer -->
//                    <div style="background: #f8fafc; padding: 32px; text-align: center; border-top: 1px solid #e5e7eb;">
//                        <p style="font-size: 14px; color: #6b7280; line-height: 1.6;">%s</p>
//                        <p style="font-size: 14px; color: #6b7280; line-height: 1.6; margin-top: 16px;">
//                            Manage your notification preferences <a href="%s" style="color: #1e6ae6; text-decoration: none;">here</a>
//                        </p>
//                        <p style="font-size: 14px; color: #6b7280; line-height: 1.6; margin-top: 24px;">
//                            © 2025 HomePod. All rights reserved.
//                        </p>
//                    </div>
//                </div>
//            </body>
//            </html>
//            """,
//                recipientName,
//                verificationCode,
//                expirationMinutes,
//                verificationUrl,
//                supportUrl,
//                companyAddress,
//                settingsUrl
//        );
//    }
//}
