String.prototype.padRight = String.prototype.padRight || function(l,c) {return this+Array(l-this.length+1).join(c||" ")}

window.PostStatus = {
    None: 0,
    UserPending: 1,
    UserError: 2,
    Ready: 3,
    AdvisorProcessing: 4,
    AdvisorEvaluated: 5,
    UserConversation :6,
    AdvisorConversation: 7,
    ClosedByUser: 8,
    ClosedByRedo: 9
}

window.PostType = {
    Speaking: 0,
    Writing: 1
}

window.PostHelper = {
    buildIndex: function(uid, val) {
        return uid.padRight(48) + (val + "").padRight(4);
    }
}

function Post (obj) {
    BaseEntity.call(this, {
        created_date: 0,
        created_by: "",
        last_modified_date: 0,
        last_modified_by: "",

        //data
        title: "",
        type: PostType.Speaking,
        audio: "",
        text: "",
        ref_topic: "",
        advisor_id: "",
        hasRead: true,
        next: "",
        prev: "",
        satisfy_score: 0,
        score: 0,
        status: PostStatus.None,

        //indexing
        index_user_status: "",
        index_user_type: ""
    }, obj);
}

Post.prototype = Object.create(BaseEntity.prototype);
Post.prototype.constructor = Post;

Post.prototype.computeIndex = function() {
    this.data.index_user_status = PostHelper.buildIndex(this.data.created_by, this.data.status);
    this.data.index_user_type = PostHelper.buildIndex(this.data.created_by, this.data.type);
    this.data.index_advisior_status = PostHelper.buildIndex(this.data.advisor_id, this.data.status);
    return this;
}
