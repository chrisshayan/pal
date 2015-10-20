function School (obj) {
    BaseEntity.call(this, {
        name: "",
        address: "",
        city: ""
    }, obj);
}
School.prototype = Object.create(BaseEntity.prototype);
School.prototype.constructor = School;
