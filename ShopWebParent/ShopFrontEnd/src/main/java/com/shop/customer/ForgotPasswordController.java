package com.shop.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shop.Utility;
import com.shop.common.entity.Customer;
import com.shop.common.exception.CustomerNotFoundException;
import com.shop.setting.EmailSettingBag;
import com.shop.setting.SettingService;

@Controller
public class ForgotPasswordController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		
		return "customers/forgot_password_form";
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		try {
			String token = customerService.updateResetPasswordToken(email);
			String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(link, email);
			
			model.addAttribute("message", "Đường dẫn đặt lại mật khẩu đã được gửi đến email của bạn."
					+ " Vui lòng kiểm tra Hộp thư đến hoặc Thư rác.");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException |MessagingException e) {
			model.addAttribute("error", "Không thể gửi email.");
		}
		return "customers/forgot_password_form";
	}
	
	private void sendEmail(String link, String email) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = email;
		String subject = "Đường dẫn thay đổi mật khẩu tài khoản Trần Anh eShop của bạn.";
		
		String content = "<p>Xin chào,</p>"
				+ "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu Trần Anh eShop của bạn.</p>"
				+ "<p>Truy cập đường dẫn dưới đây để thay đổi mật khẩu của bạn:</p>"
				+ "<p><a href=\""+ link +"\">Đổi mật khẩu</a></p>"
				+ "<br>"
				+ "<p>Bỏ qua email này nếu bạn nhớ mật khẩu, "
				+ "hoặc bạn không yêu cầu thay đổi này.<p>";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		helper.setText(content, true);
		mailSender.send(message);
	}
	
	@GetMapping("/reset_password")
	public String showResetForm(@Param("token") String token, Model model) {
		Customer customer = customerService.getByResetPasswordToken(token);
		if(customer != null) {
			model.addAttribute("token", token);
		} else {
			model.addAttribute("pageTitle", "Mã xác thực không hợp lệ");
			model.addAttribute("message", "Mã xác thực không hợp lệ");
			return "message";
		}
		return "customers/reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String processResetForm(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			customerService.updatePassword(token, password);
			model.addAttribute("pageTitle", "Đặt lại mật khẩu");
			model.addAttribute("title", "Đặt lại mật khẩu");
			model.addAttribute("message", "Mật khẩu của bạn đã được thay đổi thành công.");
			return "message";
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "Mã xác thực không hợp lệ");
			model.addAttribute("message", e.getMessage());
			return "message";
		}
	}
}
