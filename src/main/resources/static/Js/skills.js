const skillInput = document.getElementById("skillSearch");
const suggestionBox = document.getElementById("skillSuggestions");

skillInput.addEventListener("input", function(){

    let keyword = this.value;

    if(keyword.length < 1){
        suggestionBox.innerHTML = "";
        return;
    }

    fetch(`/skillSuggestions?keyword=${keyword}`)
    .then(response => response.json())
    .then(data => {

        suggestionBox.innerHTML = "";

        data.forEach(skill => {

            let li = document.createElement("li");
            li.className = "list-group-item";
            li.textContent = skill;

            li.onclick = () => {
                skillInput.value = skill;
                suggestionBox.innerHTML = "";
            }

            suggestionBox.appendChild(li);

        });

    });

});