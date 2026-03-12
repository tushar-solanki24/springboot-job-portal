function toggleLoginPassword(){

    const password = document.getElementById("loginPassword");
    const icon = document.getElementById("loginEyeIcon");

    if(password.type === "password"){
        password.type = "text";
        icon.classList.replace("bi-eye","bi-eye-slash");
    }else{
        password.type = "password";
        icon.classList.replace("bi-eye-slash","bi-eye");
    }

}