package site.termterm.api.global.home_forward;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    @Value("${home.url}")
    private String homeUrl;

    @GetMapping("/")
    public RedirectView homeForward(){
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(homeUrl);

        return redirectView;
    }
}
