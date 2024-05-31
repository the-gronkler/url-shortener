package pl.edu.pjwstk.s28259.tpo10.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.pjwstk.s28259.tpo10.dto.LinkRequest;
import pl.edu.pjwstk.s28259.tpo10.model.Link;
import pl.edu.pjwstk.s28259.tpo10.service.LinkService;

import java.util.Optional;

@Controller
public class WebpageController {

    private final LinkService linkService;

    public WebpageController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("links", linkService.getAllLinksAsDto() );
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView newLink() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("newLink");
        modelAndView.addObject("linkRequest", new LinkRequest());
        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView newLink(@ModelAttribute @Valid LinkRequest linkRequest,
                                BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("newLink");
            modelAndView.addObject("linkRequest", linkRequest);
            modelAndView.addObject("errors", bindingResult.getAllErrors());
        }
        else {
            linkService.save(linkRequest);
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView updateLink(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Link> optionalLink = linkService.findLinkById(id);
        if (optionalLink.isPresent()) {
            Link link = optionalLink.get();
            if (link.hasNoPassword()) {
                modelAndView.addObject("errorMessage", "This link is not password protected and cannot be edited.");
                modelAndView.setViewName("error");
                return modelAndView;
            }

            LinkRequest linkRequest = new LinkRequest();
            linkRequest.setName(link.getName());
            linkRequest.setTargetUrl(link.getTargetUrl());
            linkRequest.setPassword(link.getPassword());

            modelAndView.addObject("linkRequest", linkRequest);
            modelAndView.addObject("id", id);
            modelAndView.setViewName("patchLink");
        }
        else { // no such link
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }

    @PostMapping("/edit{id}")
    public ModelAndView updateLink(@ModelAttribute @Valid LinkRequest linkRequest,
                                   BindingResult bindingResult,
                                   @PathVariable String id,
                                   @RequestParam String passwordConfirmation) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Link> optionalLink = linkService.findLinkById(id);
        if (optionalLink.isPresent()) {
            Link link = optionalLink.get();
            if (!link.getPassword().equals(passwordConfirmation)) {
                modelAndView.setViewName("patchLink");
                modelAndView.addObject("linkRequest", linkRequest);
                modelAndView.addObject("errors", "Incorrect password");
                return modelAndView;
            }
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("patchLink");
            modelAndView.addObject("linkRequest", linkRequest);
            modelAndView.addObject("errors", bindingResult.getAllErrors());
        } else {
            linkService.updateLink(id, linkRequest);
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }
}