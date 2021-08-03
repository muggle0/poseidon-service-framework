package com.muggle.psf.controller;

import com.muggle.psf.entity.ProjectMessage;
import com.muggle.psf.factory.PoseidonCodeFactory;
import com.muggle.psf.genera.SimpleCodeGenerator;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller("/psf")
public class GenerateController {

    @PostMapping("/generator")
    public ModelAndView createProject(@RequestBody ProjectMessage projectMessage){
        PoseidonCodeFactory.init(new SimpleCodeGenerator());
        try {
            PoseidonCodeFactory.createProject(projectMessage);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return new ModelAndView("");
        }
        return new ModelAndView("");

    }


    @GetMapping("/page")
    public ModelAndView createProject(){
        return new ModelAndView("/test.ftl");
    }

}
