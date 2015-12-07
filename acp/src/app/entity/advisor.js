function Advisor (obj) {
    BaseEntity.call(this, {
        avatar: "",
        email: "",
        display_name: "",
        first_name: "",
        last_name: "",
        teaching_exp: 0,
        city: "",
        address: "",
        school: "",
        points: 0,
        rate1: 0,
        rate2: 0,
        rate3: 0,
        rate4: 0,
        rate5: 0
    }, obj);
}
Advisor.prototype = Object.create(BaseEntity.prototype);
Advisor.prototype.constructor = Advisor;
