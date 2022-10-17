package api.accont.services;

import api.accont.model.UserModel;
import api.accont.repository.UserRepository;
import api.aconnt.data.DetailUserData;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DetailsUserServicesIMPL implements UserDetailsService {

    private final UserRepository repository;

    public DetailsUserServicesIMPL(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> user = repository.findByLogin(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User [" + username + "] not found");
        }

        return new DetailUserData(user);
    }


}
