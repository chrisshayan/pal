function Advisor (obj) {
    BaseEntity.call(this, {
        email: "",
        display_name: "",
        school_id: ""
    }, obj);
}
Advisor.prototype = Object.create(BaseEntity.prototype);
Advisor.prototype.constructor = Advisor;
