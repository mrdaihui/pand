package com.hui.pand.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.hui.pand.entity.RoleEntity;
import com.hui.pand.entity.UserEntity;
import com.hui.pand.utils.SnowflakeIdWorker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author daihui
 * @date 2020/5/7 13:41
 */
public class User implements UserDetails{

    private UserEntity user;

    @JsonIgnore
    private List<RoleEntity> roles;

    private boolean isAccountNonLocked;

    public User(UserEntity userEntity){
        this.user = userEntity;
        this.roles = userEntity.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roleCodes = new ArrayList<>();
        roles.stream().forEach(roleEntity -> {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+roleEntity.getRoleCode());
            roleCodes.add(grantedAuthority);
        });
        return roleCodes;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getUsername();
    }

    //isAccountNonExpired表示账户没有过期，返回true表示认证成功，返回false代表过期了。如果在系统中没有关于这个的逻辑，可以永远返回true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { //锁定
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() { //密码过期
        return true;
    }

    @Override
    public boolean isEnabled() { //启用
        return true;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity userEntity) {
        this.user = userEntity;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }


    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user1 = (User) o;
        return Objects.equal(user.getUsername(), user1.user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user.getUsername());
    }
}
