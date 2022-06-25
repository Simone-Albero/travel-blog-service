package it.uniroma3.travelblog.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.travelblog.model.Credentials;
import it.uniroma3.travelblog.model.User;
import it.uniroma3.travelblog.presentation.FileStorer;
import it.uniroma3.travelblog.service.CredentialsService;
import it.uniroma3.travelblog.service.UserService;
import it.uniroma3.travelblog.validator.CredentialsValidator;
import it.uniroma3.travelblog.validator.UserValidator;

@Controller
public class AuthController {
	
	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired
	private UserValidator userValidator;
	
	@Autowired
	private CredentialsValidator credentialsValidator;

	@Autowired private UserService userService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "signUp";
	}
	
	@GetMapping("/login")
	public String showLoginForm(Model model) {
		return "login";
	}
	
	@GetMapping("/logout")
	public String logout(Model model) {
		return "index";
	}
	
    @GetMapping("/default")
    public String defaultAfterLogin(Model model) {
        
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
//    	if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
//            return "admin/home";
//        }
//        return "index";
    	return "redirect:/";
    }
    
    @PostMapping("/registration/validate")
    public String registerUser(@ModelAttribute("user") User user, BindingResult userBindingResult, @ModelAttribute("credentials") Credentials credentials, BindingResult credentialsBindingResult, @RequestParam("file")MultipartFile file, Model model) {

        this.userValidator.validate(user, userBindingResult);
        this.credentialsValidator.validate(credentials, credentialsBindingResult);

        if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
        	user.setImg(FileStorer.store(file, credentials.getDirectoryName()));
        	
            credentials.setUser(user);
            credentialsService.save(credentials);
            return "registrationSuccessful";
        }
        return "signUp";
    }
    

    @GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
		Credentials credentials = this.credentialsService.findById(id);
		FileStorer.dirEmptyEndDelete(credentials.getDirectoryName());
		this.credentialsService.deleteById(id);
		try {
			request.logout();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return "index";
	}
    
	@GetMapping("/user/delete/image/{id}")
	public String deleteImage(@PathVariable("id") Long id, Model model) {
		Credentials credentials = this.credentialsService.findById(id);
		FileStorer.removeImgAndDir(credentials.getDirectoryName(), credentials.getUser().getImg());
		credentials.getUser().setImg(null);			
		this.credentialsService.update(credentials);
		
		return this.getProfile(model);
	}
    
    
    @GetMapping("/admin/promote")
    public String promoteUser() {
    	return "/admin/promote";
    }
    
    @PostMapping("/admin/promote/finalize")
    public String promotion(@ModelAttribute("username") String username, BindingResult userBindingResult, Model model) {
    	this.credentialsService.promote(username);
    	return "admin/home";
    }
    
    @GetMapping("/profile")
    public String getProfile(Model model) {
    	
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String username = ((UserDetails)principal).getUsername();
    	
    	Credentials credentials = this.credentialsService.findByUsername(username);
    	
		model.addAttribute("user", credentials.getUser());
    	return "profile";
    }
    
    @GetMapping("/profile/{id}")
    public String getProfile(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", this.userService.findById(id));
    	return "profile";
    }
    
    @PostMapping("/profile/modify")
	public String updateChef(@ModelAttribute("credentials")Credentials credentials, @RequestParam("file")MultipartFile file, BindingResult bindingResult, Model model) {
    	this.userValidator.validate(credentials.getUser(), bindingResult);
    	this.credentialsValidator.validate(credentials, bindingResult);
		
    	if(!bindingResult.hasErrors()) {
			FileStorer.dirRename(this.credentialsService.findById(credentials.getId()).getDirectoryName() , credentials.getDirectoryName());
			
			if(!file.isEmpty()) {
				FileStorer.removeImgAndDir(credentials.getDirectoryName(), credentials.getUser().getImg());
				credentials.getUser().setImg(FileStorer.store(file, credentials.getDirectoryName()));
			}
			
			credentialsService.update(credentials);
			
			return "index";
		}
		else return "/profile";
	}
}
