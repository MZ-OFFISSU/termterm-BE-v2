### Auth
- [ ] POST /v1/apple-callback
- [x] POST /v1/auth/{socialType}
- [x] POST /v1/auth/token/refresh
- [x] GET /v1/social-auth


### Comment
- [ ] POST /v1/comment
- [ ] PUT /v1/comment/accept/{id}
- [ ] PUT /v1/comment/dislike/{id}
- [ ] PUT /v1/comment/like/{id}
- [ ] GET /v1/comment/list
- [ ] PUT /v1/comment/reject/{id}
- [ ] POST /v1/comment/report
- [ ] PUT /v1/comment/report/completed/{id}
- [ ] GET /v1/comment/report/list
- [ ] PUT /v1/comment/reported/{id}
- [ ] PUT /v1/comment/wait/{id}


### Curation
- [ ] DELETE /v1/folder/{folderId}
- [ ] GET /v1/folder/{folderId}/including/{termId}
- [ ] GET /v1/folder/detail/each/{folderId}
- [ ] GET /v1/folder/detail/sum/{folderId}
- [ ] PUT /v1/folder/info
- [ ] GET /v1/folder/list
- [ ] POST /v1/folder/new
- [ ] GET /v1/folder/related-info
- [ ] POST /v1/folder/term
- [ ] DELETE /v1/folder/term
- [ ] GET /v1/folder/term/random-10

### Folder
- [x] DELETE /v1/folder/{folderId}
- [ ] GET /v1/folder/{folderId}/including/{termId}
- [ ] GET /v1/folder/detail/each/{folderId}
- [x] GET /v1/folder/detail/sum/{folderId}
- [x] PUT /v1/folder/info
- [x] GET /v1/folder/list
- [x] POST /v1/folder/new
- [ ] GET /v1/folder/related-info
- [x] POST /v1/folder/term
- [x] DELETE /v1/folder/term
- [ ] GET /v1/folder/term/random-10

### Home - 상단 Title (우선순위 제일 아래...)
- [ ] GET /v1/home/subtitle
- [ ] POST /v1/home/subtitle
- [ ] DELETE /v1/home/subtitle
- [ ] GET /v1/home/title


### Inquiry
- [ ] POST /v1/inquiry
- [ ] GET /v1/inquiry/{id}
- [ ] GET /v1/inquiry/list
- [ ] PUT /v1/inquiry/to-completed/{id}
- [ ] PUT /v1/inquiry/to-waiting/{id}


### Member
- [x] GET /v1/member/info
- [x] PUT /v1/member/info
- [x] PUT /v1/member/info/category
- [x] GET /v1/member/info/profile-image
- [x] DELETE /v1/member/info/profile-image
- [x] GET /v1/member/info/profile-image/presigned-url
- [x] GET /v1/member/info/profile-image/sync  __-------------> PUT__
- [ ] GET /v1/member/nickname/check
- [x] GET /v1/member/withdraw  __-------------> PUT__


### Point
- [ ] GET /v1/point/current
- [ ] GET /v1/point/history
- [ ] PUT /v1/point/pay/curation/{id}
- [ ] PUT /v1/point/pay/folder


### Quiz
- [ ] GET /v1/quiz/daily
- [ ] GET /v1/quiz/final-quiz-review
- [ ] GET /v1/quiz/review
- [ ] GET /v1/quiz/status
- [ ] POST /v1/quiz/result


### Term
- [ ] GET /v1/term/daily
- [ ] GET /v1/term/detail/{id}
- [ ] POST /v1/term/list
- [ ] GET /v1/term/search/{name}  // TODO : Response 에 BookmarkStatus 추가