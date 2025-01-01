package com.alphaStore.alphaS3

import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/seeder")
class HealthCheck {

    @GetMapping("/healthCheck")
    fun healthCheck(model: Model): String {
        model.addAttribute("message","I Am Up")
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Pop Up Fade Animation</title>" +
                "<style>" +
                "        body, html {" +
                "    height: 100%;" +
                "    margin: 0;" +
                "    display: flex;" +
                "    justify-content: center;" +
                "    align-items: center;" +
                "    background-color: #f0f0f0;" +
                "}" +
                "        .popup-fade {" +
                "    font-family: 'Arial', sans-serif;" +
                "    font-size: 2em;" +
                "    text-align: center;" +
                "    opacity: 0; /* Start with text invisible */" +
                "    animation: fadeInOut 2s infinite; /* Run the fadeInOut animation every 2 seconds */" +
                "}" +
                "/* Keyframe for fade-in and fade-out effect */" +
                "@keyframes fadeInOut {" +
                "    0% {" +
                "        opacity: 0;" +
                "    }" +
                "    50% {" +
                "        opacity: 1; /* Text fully visible at the halfway point */" +
                "    }" +
                "    100% {" +
                "        opacity: 0; /* Text fades out at the end */" +
                "    }" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='popup-fade'>I AM UP ~ Coded By <a href='https://github.com/AlphaTanmoy'>AlphaTanmoy</a> @AlphaStore</p></div>" +
                "</body>" +
                "</html>"

    }

}