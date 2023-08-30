package vn.iostar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.entity.Account;
import vn.iostar.service.AccountService;

@RestController
public class HomeController {

	@Autowired
	AccountService accountService;

	@GetMapping("/")
	public List<Account> findAll() {
		return accountService.findAll();

	}
}
