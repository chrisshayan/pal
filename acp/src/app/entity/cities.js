function City (obj) {
    BaseEntity.call(this, {
        nation: "",
        en: "",
        vi: "",
    }, obj);
}
City.prototype = Object.create(BaseEntity.prototype);
City.prototype.constructor = City;
