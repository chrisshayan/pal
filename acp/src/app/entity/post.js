var PostStatus = {
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

var PostType = {
    Speaking: 0,
    Writing: 1
}

function Post () {
    this.property = {
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
        hasRead: false,
        next: "",
        prev: "",
        satisfy_score: 0,
        score: 0,
        status: PostStatus.None,

        //indexing
        index_user_status: "",
        index_user_type: ""
    }

    this.data = JSON.parse(JSON.stringify(this.property));
}
Post.prototype.set = function(k, v) {
    this.data[k] = v;
    return this;
}

Post.prototype.doCreate = function(by) {
    this.data.created_by = by;
    this.data.created_date = Date.now();
    this.data.last_modified_by = by;
    this.data.last_modified_date = Date.now();
    this.computeIndex();
    return this;
}

Post.prototype.doModify = function(by) {
    this.data.last_modified_by = by;
    this.data.last_modified_date = Date.now();
    this.computeIndex();
    return this;
}

String.prototype.padRight = String.prototype.padRight || function(l,c) {return this+Array(l-this.length+1).join(c||" ")}

Post.prototype.computeIndex = function() {
    this.data.index_user_status = this.data.created_by.padRight(48) + (this.data.status + "").padRight(4);
    this.data.index_user_type = this.data.created_by.padRight(48) + (this.data.type + "").padRight(4);
    return this;
}
