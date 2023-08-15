let validationMessages

getValidationMessages().then(v => {
    validationMessages = v;
})

async function getValidationMessages() {
    let messages = await request(`/messages-validation`, 'GET');
    return messages.response;
}