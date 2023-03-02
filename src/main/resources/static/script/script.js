const mainImages = Array.from(document.querySelectorAll(".selected-post-main-img"));
const smallImages = Array.from(document.querySelectorAll(".selected-post-img"));
const buttonShowImage = Array.from(document.querySelectorAll(".images__block-main-button"));

const showImage = (button, index) => {
    if (button != null) {
        button.addEventListener("click", () => {
            for (let i = 0; i < mainImages.length; i++) {
                mainImages[i].classList.remove("_active")
                buttonShowImage[i].classList.remove("_active");
            }
            mainImages[index].classList.add("_active")
            buttonShowImage[index].classList.add("_active");
        });
    }
};
const buttonOfHiddenImage = (button) => {
    if (button != null) {
        button.addEventListener("click", () => {
            for (let i = 0; i < mainImages.length; i++) {
                mainImages[i].classList.remove("_active")
            }
            button.classList.remove("_active")
        });
    }
};
smallImages.forEach(showImage);
buttonShowImage.forEach(buttonOfHiddenImage);

const comments = Array.from(document.querySelectorAll(".comments__post"));
const showMoreButtons = document.querySelector(".comments__button");

const showComments = () => {
    if (comments != null) {
        if (comments.length < 3) {
            for (let i = 0; i < comments.length; i++) {
                comments[i].classList.add("_active");
            }
        }
        if (comments.length >= 3) {
            for (let i = 0; i < 3; i++) {
                comments[i].classList.add("_active");
            }
        }
    }
}
const showCommentsAfterPressButton = () => {
    if (comments != null && showMoreButtons != null) {
        showMoreButtons.addEventListener("click", () => {
            let lengthOfActive = 0;
            let lengthNewActiveList;
            for (let i = 0; i < comments.length; i++) {
                if (comments[i].classList.contains("_active")) {
                    lengthOfActive++;
                }
            }
            if (lengthOfActive + 5 > comments.length) {
                lengthNewActiveList = comments.length;
                showMoreButtons.classList.add("_hidden");
            }
            else {
                lengthNewActiveList = lengthOfActive + 5;
            }
            for (let i = 0; i < lengthNewActiveList; i++) {
                comments[i].classList.add("_active");
            }
        });
    }
}
const checkLengthOfList = () => {
    if (comments != null && showMoreButtons != null && comments.length <= 3) {
        showMoreButtons.classList.add("_hidden");
    }
};
showComments();
showCommentsAfterPressButton();
checkLengthOfList();
