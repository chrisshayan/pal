function Advisor (obj) {
    BaseEntity.call(this, {
        avatar: "",
        email: "",
        display_name: "",
        first_name: "",
        last_name: "",
        exp: 0,
        city: "",
        address: "",
        school: "",
        points: ""
    }, obj);
}
Advisor.prototype = Object.create(BaseEntity.prototype);
Advisor.prototype.constructor = Advisor;
