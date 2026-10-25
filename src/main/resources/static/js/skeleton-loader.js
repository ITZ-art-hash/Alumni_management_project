document.addEventListener('DOMContentLoaded', function () {
    var skeleton = document.getElementById('skeleton-loader');
    var content = document.getElementById('page-content');
    if (!skeleton || !content) {
        return;
    }

    var revealed = false;
    function reveal() {
        if (revealed) {
            return;
        }
        revealed = true;
        skeleton.classList.add('d-none');
        content.classList.remove('d-none');
    }

    window.addEventListener('load', reveal);
    window.addEventListener('pageshow', reveal);
    setTimeout(reveal, 600);

    content.querySelectorAll('a.page-link, form').forEach(function (el) {
        el.addEventListener(el.tagName === 'FORM' ? 'submit' : 'click', function () {
            content.classList.add('d-none');
            skeleton.classList.remove('d-none');
        });
    });
});
