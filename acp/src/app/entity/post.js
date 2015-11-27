String.prototype.padRight = String.prototype.padRight || function(l,c) {return this+Array(l-this.length+1).join(c||" ")}
String.prototype.padLeft = String.prototype.padLeft || function (n,str){return Array(n-String(this).length+1).join(str||' ')+this;}

window.PostStatus = {
    None: 0,
    UserPending: 1,
    UserError: 2,
    Sync: 3,
    Ready: 4,
    AdvisorProcessing: 5,
    AdvisorEvaluated: 6,
    UserConversation :7,
    AdvisorConversation: 8,
    ClosedByUser: 9,
    ClosedByRedo: 10
}

window.PostType = {
    Speaking: 0,
    Writing: 1
}

window.PostHelper = {
    buildIndex: function(uid, val) {
        return uid.padRight(48) + (val + "").padLeft(4);
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
        audio: "",
        text: "",
        ref_topic: "",
        advisor_id: "",
        has_read: true,
        next: "",
        prev: "",
        score: 0,
        status: PostStatus.None,

        //indexing
        index_advisior_status: ""
    }, obj);
}

Post.prototype = Object.create(BaseEntity.prototype);
Post.prototype.constructor = Post;

Post.prototype.computeIndex = function() {
    this.data.index_advisior_status = PostHelper.buildIndex(this.data.advisor_id, this.data.status);
    return this;
}
