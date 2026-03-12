const searchInput = document.getElementById("jobSearch");
const suggestionBox = document.getElementById("suggestions");

searchInput.addEventListener("input", function(){

    let keyword = this.value;

    if(keyword.length < 2){
        suggestionBox.innerHTML = "";
        return;
    }

    fetch(`/jobSuggestions?keyword=${keyword}`)
    .then(response => response.json())
    .then(data => {

        suggestionBox.innerHTML = "";

        data.forEach(job => {

            let li = document.createElement("li");
            li.className = "list-group-item";
            li.textContent = job.jobTitle;

            li.onclick = () => {
                searchInput.value = job.jobTitle;
                suggestionBox.innerHTML = "";
            }

            suggestionBox.appendChild(li);

        });

    });

});


/* CLOSE DROPDOWN WHEN CLICKING OUTSIDE */

document.addEventListener("click", function(event){

    if(!searchInput.contains(event.target) &&
       !suggestionBox.contains(event.target)){

        suggestionBox.innerHTML = "";

    }

});