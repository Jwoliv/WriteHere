const collectionOfSmallImages = Array.from(document.querySelectorAll(".selected-post__wrapper-small-image"));
const collectionOfLargeImages = Array.from(document.querySelectorAll(".selected-post__wrapper-large-image"));
const collectionOfTheButtonsOfImages = Array.from(document.querySelectorAll(".selected-post__close-icon"));

const pressTheImage = (button, index) => {
    if (collectionOfLargeImages != null) {
        button.addEventListener("click", () => {
            for (let i = 0; i < collectionOfLargeImages.length; i++) {
                collectionOfLargeImages[i].classList.add("_hidden");
            }
            collectionOfLargeImages[index].classList.remove("_hidden");
        });
    }
};
collectionOfSmallImages.forEach(pressTheImage);


const pressTheButton = (button, index) => {
    if (collectionOfLargeImages != null) {
        button.addEventListener("click", () => {
            collectionOfLargeImages[index].classList.add("_hidden");
        });
    }
}
collectionOfTheButtonsOfImages.forEach(pressTheButton);



const collectionsOfReportFormForComment = Array.from(document.querySelectorAll(".comment__form-report"));
const buttonForShowReportsForComment = Array.from(document.querySelectorAll(".show-report-of-comment"));
const showReportFormForComment = (button, index) => {
    if (collectionsOfReportFormForComment != null && buttonForShowReportsForComment != null) {
        button.addEventListener("click", () => {
            collectionsOfReportFormForComment[index].classList.add("_active");
            buttonForShowReportsForComment[index].classList.add("_hidden");
        });
    }
};
buttonForShowReportsForComment.forEach(showReportFormForComment);


const comments = Array.from(document.querySelectorAll(".comments__comment"));
const buttonShowMoreComments = document.querySelector(".show-more-comments");

const showComments = () => {
    if (comments != null && buttonShowMoreComments != null) {
        let length;
        if (comments.length <= 3) {
            buttonShowMoreComments.classList.add("_hidden");
            length = comments.length;
        }
        else { length = 3 }
        for (let i = 0; i < length; i++) {
            comments[i].classList.add("_show");
        }
    }
};
showComments();

const showMoreCommentsUseButton = () => {
    if (buttonShowMoreComments != null && comments != null) {
        buttonShowMoreComments.addEventListener("click", () => {
            let countOfActiveComment = 0;
            let lengthOfNewActiveListOfComment;
            for (let i = 0; i < comments.length; i++) {
                if (comments[i].classList.contains("_show")) countOfActiveComment++;
            }
            if (countOfActiveComment + 5 >= comments.length) lengthOfNewActiveListOfComment = comments.length;
            else lengthOfNewActiveListOfComment = countOfActiveComment + 5;

            for (let i = countOfActiveComment; i < lengthOfNewActiveListOfComment; i++) {
                comments[i].classList.add("_show");
            }
            if (lengthOfNewActiveListOfComment === comments.length) {
                buttonShowMoreComments.classList.add("_hidden");
            }
        });
    }
};
showMoreCommentsUseButton();


const showButtonFormNewCommentOfPost = document.querySelector(".selected-post__show-comment");
const showButtonFormNewReportOfPost = document.querySelector(".selected-post__show-report");

const formNewCommentOfPost = document.querySelector(".selected-post__new-comment");
const formNewReportOfPost = document.querySelector(".post__form-report");

const showFormsOfPost = () => {
    if (showButtonFormNewCommentOfPost != null && formNewCommentOfPost != null) {
        showButtonFormNewCommentOfPost.addEventListener("click", () => {
            formNewCommentOfPost.classList.add("_show");
            showButtonFormNewCommentOfPost.classList.add("_hidden");
        });
    }
    if (showButtonFormNewReportOfPost != null && formNewReportOfPost != null) {
        showButtonFormNewReportOfPost.addEventListener("click", () => {
            formNewReportOfPost.classList.add("_show");
            showButtonFormNewReportOfPost.classList.add("_hidden");
        });
    }
};
showFormsOfPost();

const loginShowButton = document.querySelector(".login-show-button");
const singInShowButton = document.querySelector(".sing-in-show-button");
const loginForm = document.querySelector(".login-form");
const singInForm = document.querySelector(".sing-in-form");

const showFormOnTheLoginPage = () => {
    if (loginShowButton != null && loginForm != null && singInShowButton != null && singInForm != null) {
        loginShowButton.addEventListener("click", () => {
            loginForm.classList.add("_show");
            loginShowButton.classList.add("_active");
            singInShowButton.classList.remove("_active");
            singInForm.classList.remove("_show");
        });
        singInShowButton.addEventListener("click", () => {
            singInForm.classList.add("_show");
            singInShowButton.classList.add("_active");
            loginShowButton.classList.remove("_active");
            loginForm.classList.remove("_show");
        });
    }
};
showFormOnTheLoginPage();