document.addEventListener("DOMContentLoaded", function () {
    var quill = new Quill('#quill-editor', {
        modules: {
            toolbar: '#toolbar'
        },
        theme: 'snow'
    });

    if (window.initialDescription && window.initialDescription.length > 0) {
        quill.root.innerHTML = window.initialDescription;
    }

    window.submitDescription = function () {
        const html = quill.root.innerHTML;
        document.getElementById('description-input').value = DOMPurify.sanitize(html, {
            ALLOWED_TAGS: ['b', 'i', 'u', 'a', 'ul', 'ol', 'li', 'strong', 'em', 'br'],
            ALLOWED_ATTR: ['target', 'rel']
        });
        return true;
    };
});