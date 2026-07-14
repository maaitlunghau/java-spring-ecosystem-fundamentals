package com.maaitlunghau.__fullstack_user_management.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maaitlunghau.__fullstack_user_management.entity.SocialAccount;
import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.repository.SocialAccountRepository;
import com.maaitlunghau.__fullstack_user_management.repository.UserRepository;

/**
 * Account linking (Hướng B) — điểm khó nhất Phase 3. Sau khi Auth0 xác thực social,
 * quyết định: danh tính này ứng với User nào trong DB nội bộ?
 */
@Service
@Transactional
public class SocialLoginService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;

    public SocialLoginService(UserRepository userRepository,
                              SocialAccountRepository socialAccountRepository) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
    }

    /**
     * @param provider        vd "google-oauth2", "github" (parse từ sub)
     * @param providerUserId  sub của Auth0 (định danh duy nhất bên provider)
     * @param email           email từ ID token
     * @param emailVerified   provider có xác thực email không
     * @param fullName        tên hiển thị
     * @param avatarUrl       ảnh đại diện
     */
    public User findOrCreate(String provider, String providerUserId,
                             String email, boolean emailVerified,
                             String fullName, String avatarUrl) {

        // 1. Đã từng login social này → dùng luôn user đã liên kết
        return socialAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
            .map(SocialAccount::getUser)
            .orElseGet(() -> {
                // 2. Chưa liên kết → thử ghép theo email, CHỈ khi provider đã verify email
                //    (nếu không, kẻ xấu có thể chiếm account bằng email người khác chưa verify)
                if (emailVerified && email != null) {
                    User existing = userRepository.findByEmail(email).orElse(null);
                    if (existing != null) {
                        socialAccountRepository.save(new SocialAccount(existing, provider, providerUserId));
                        return existing;
                    }
                }
                // 3. Tạo user mới — password null (chỉ social), role mặc định USER
                User user = new User(email, null, fullName);
                user.updateProfile(fullName, avatarUrl);
                if (emailVerified) {
                    user.markEmailVerified();
                }
                userRepository.save(user);
                socialAccountRepository.save(new SocialAccount(user, provider, providerUserId));
                return user;
            });
    }
}
