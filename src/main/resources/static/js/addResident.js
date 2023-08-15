function addResident(target) {
    modal.style.display = 'block'

    modalTitle.textContent += target.parentElement.parentElement.children[1].textContent
    let form = document.getElementById('add-resident');

    let formAction = form.attributes[1];
    formAction.value = formAction.value.replace('/home/', `/home${target.attributes[0].value}/`);
}

function fieldValidation(target, name) {

    let divError = document.getElementById(name)

    if (target.value.length < 3 || target.value.length  > 30) {
        divError.style.visibility = "visible"
        divError.firstElementChild.textContent = validationMessages[2].value;
    } else {
        divError.style.visibility = "hidden"
    }
}




