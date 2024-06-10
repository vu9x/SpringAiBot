package vn.vt.service;

import vn.vt.entity.AppUser;

public interface AppUserService {

    String registerUser(AppUser appUser);

    String setEmail(AppUser appUser, String email);
}
