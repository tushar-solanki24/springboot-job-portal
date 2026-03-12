function togglePassword() {

    const password = document.getElementById("password");
    const icon = document.getElementById("eyeIcon");

    if(password.type === "password"){
        password.type = "text";
        icon.classList.replace("bi-eye","bi-eye-slash");
    }else{
        password.type = "password";
        icon.classList.replace("bi-eye-slash","bi-eye");
    }
}

function toggleConfirmPassword() {

    const confirm = document.getElementById("confirmPassword");
    const icon = document.getElementById("confirmEyeIcon");

    if(confirm.type === "password"){
        confirm.type = "text";
        icon.classList.replace("bi-eye","bi-eye-slash");
    }else{
        confirm.type = "password";
        icon.classList.replace("bi-eye-slash","bi-eye");
    }
}

document.getElementById("confirmPassword").addEventListener("keyup", function(){

    const password = document.getElementById("password").value;
    const confirm = document.getElementById("confirmPassword").value;

    if(password !== confirm){
        document.getElementById("passwordError").innerText = "Passwords do not match";
    }else{
        document.getElementById("passwordError").innerText = "";
    }

});