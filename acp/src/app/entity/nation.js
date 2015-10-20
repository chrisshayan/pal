function Nation (obj) {
    BaseEntity.call(this, {
        en: "",
        vi: "",
    }, obj);
}
Nation.prototype = Object.create(BaseEntity.prototype);
Nation.prototype.constructor = Nation;
