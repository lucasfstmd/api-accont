package apiaccont.controller;

import apiaccont.model.UserModel;
import apiaccont.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserController(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<UserModel>> listarUsuarios(){
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/salvar")
    public ResponseEntity<UserModel> salvar(@RequestBody UserModel user){
        user.setPassword(encoder.encode(user.getPassword())); // -> Encripta senha passado pelo usuario
        return  ResponseEntity.ok(repository.save(user));
    }

    @GetMapping("/validarSenha")
    public ResponseEntity<Boolean> validarSenha(@RequestParam String login, @RequestParam String password){

        Optional<UserModel> optUser = repository.findByLogin(login);
        // -> Procura o usuario, caso não seja encotrado pelo login, não autoriza
        if(optUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }


        boolean valid = encoder.matches(password, optUser.get().getPassword());
        // -> metodo matches compara a senha encriptada com a senha passada pelo usuario
        // -> Valida comparando se a senha passado no metodo é a mesma que esta no banco de dados

        HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        // -> Caso senha seja valida, HttpStatus OK, se não HttpStatus não autoriza
        return ResponseEntity.status(status).body(valid);
    }

}
