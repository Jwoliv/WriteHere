$(document).ready(function() {
    $('.like-btn').click(function(event) {
        event.preventDefault();
        const postId = $(this).data('post-id');
        const url = $(this).closest('form').attr('action');
        fetch(url, {
            method: 'PATCH',
            body: JSON.stringify({ id: postId }),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                // Оновлюємо HTML-код блоку з кількістю лайків та дизлайків
                $('#post-' + postId + ' .likes-count').html(data.likesHtml);
                $('#post-' + postId + ' .dislikes-count').html(data.dislikesHtml);
                // Додаємо/видаляємо клас "active" з кнопки
                $('.like-btn[data-post-id="' + postId + '"]').toggleClass('active');
                $('.dislike-btn[data-post-id="' + postId + '"]').removeClass('active');
            })
            .catch(error => console.error(error));
    });
    $('.dislike-btn').click(function(event) {
        event.preventDefault();
        const postId = $(this).data('post-id');
        const url = $(this).closest('form').attr('action');
        fetch(url, {
            method: 'PATCH',
            body: JSON.stringify({ id: postId }),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                $('#post-' + postId + ' .likes-count').html(data.likesHtml);
                $('#post-' + postId + ' .dislikes-count').html(data.dislikesHtml);
                $('.dislike-btn[data-post-id="' + postId + '"]').toggleClass('active');
                $('.like-btn[data-post-id="' + postId + '"]').removeClass('active');
            })
            .catch(error => console.error(error));
    });
});