window.TopicType = {
    Speaking: 0,
    Writing: 1
}

window.TopicStatus = {
    Disable: 0,
    Enable: 1
}

function Topic (obj) {
    BaseEntity.call(this, {
        title: "",
        status: TopicStatus.Enable,
        type: TopicType.Speaking,
        views: 0,
        submits: 0,
        hint: "",
        level: 0
    }, obj);
}
Topic.prototype = Object.create(BaseEntity.prototype);
Topic.prototype.constructor = Topic;
