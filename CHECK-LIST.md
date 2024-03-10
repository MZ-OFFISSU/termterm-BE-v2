### Auth
- [x] POST /v1/apple-callback
- [x] POST /v1/auth/{socialType}
- [x] POST /v1/auth/token/refresh
- [x] GET /v1/social-auth


### Comment
- [x] POST /v1/comment
- [ ] PUT /v1/comment/accept/{id}            (ADMIN)
- [x] PUT /v1/comment/dislike/{id}
- [x] PUT /v1/comment/like/{id}
- [ ] GET /v1/comment/list                   (ADMIN)
- [ ] PUT /v1/comment/reject/{id}            (ADMIN)
- [x] POST /v1/comment/report
- [ ] PUT /v1/comment/report/completed/{id}  (ADMIN)
- [ ] GET /v1/comment/report/list            (ADMIN)
- [ ] PUT /v1/comment/reported/{id}          (ADMIN)
- [ ] PUT /v1/comment/wait/{id}              (ADMIN)


### Curation
- [x] GET /v1/curation/archived
- [x] PUT /v1/curation/bookmark/{id} 
- [x] PUT /v1/curation/unbookmark/{id} 
- [x] GET /v1/curation/detail/{id}
- [X] GET /v1/curation/list
- [x] POST /v1/curation/register (ADMIN)

### Folder
- [x] DELETE /v1/folder/{folderId}
- [x] GET /v1/folder/{folderId}/including/{termId}
- [x] GET /v1/folder/detail/each/{folderId}
- [x] GET /v1/folder/detail/sum/{folderId}
- [x] PUT /v1/folder/info
- [x] GET /v1/folder/list
- [x] POST /v1/folder/new
- [x] GET /v1/folder/related-info
- [x] POST /v1/folder/term
- [x] DELETE /v1/folder/term
- [x] GET /v1/folder/term/random-10

### Home - 상단 Title (우선순위 제일 아래...)
- [ ] GET /v1/home/subtitle                    (ADMIN)
- [x] POST /v1/home/subtitle                   (ADMIN)
- [ ] DELETE /v1/home/subtitle                 (ADMIN)
- [x] GET /v1/home/title


### Inquiry
- [x] POST /v1/inquiry
- [ ] GET /v1/inquiry/{id}                     (ADMIN)
- [ ] GET /v1/inquiry/list                     (ADMIN)
- [ ] PUT /v1/inquiry/to-completed/{id}        (ADMIN)
- [ ] PUT /v1/inquiry/to-waiting/{id}          (ADMIN)


### Member
- [x] GET /v1/member/info
- [x] PUT /v1/member/info
- [x] PUT /v1/member/info/category
- [x] GET /v1/member/info/profile-image
- [x] DELETE /v1/member/info/profile-image
- [x] GET /v1/member/info/profile-image/presigned-url
- [x] GET /v1/member/info/profile-image/sync  __-------------> PUT__
- [x] GET /v1/member/nickname/check
- [x] GET /v1/member/withdraw                 __-------------> PUT__


### Point
- [x] GET /v1/point/current
- [x] GET /v1/point/history
- [x] PUT /v1/point/pay/curation/{id}
- [x] PUT /v1/point/pay/folder


### Quiz
- [x] GET /v1/quiz/daily
- [x] GET /v1/quiz/final-quiz-review
- [x] GET /v1/quiz/review
- [x] GET /v1/quiz/status
- [x] POST /v1/quiz/result


### Term
- [x] GET /v1/term/daily
- [x] GET /v1/term/detail/{id}
- [x] POST /v1/term/list
- [x] GET /v1/term/search/{name} 