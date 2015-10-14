var TopicType = {
    Speaking: 0,
    Writing: 1
}

var TopicStatus = {
    Disable: 0,
    Enable: 1
}

function Topic () {
    this.property = {
        title: "",
        status: TopicStatus.Enable,
        type: TopicType.Speaking
    };
    this.data = JSON.parse(JSON.stringify(this.property));
}

Topic.prototype.set = function(k, v) {
    this.data[k] = v;
}

Topic.prototype.doCreate = function(by) {
    this.data.created_by = by;
    this.data.created_date = Date.now();
    this.data.last_modified_by = by;
    this.data.last_modified_date = Date.now();
    this.computeIndex();
    return this;
}

Topic.prototype.doModify = function(by) {
    this.data.last_modified_by = by;
    this.data.last_modified_date = Date.now();
    this.computeIndex();
    return this;
}

Topic.prototype.computeIndex = function() {
}
