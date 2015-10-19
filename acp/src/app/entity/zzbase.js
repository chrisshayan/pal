function BaseEntity(property, obj) {
    this.property = property;
    if (obj) {
        this.data = JSON.parse(JSON.stringify(this.property));
        for (var k in obj) {
            this.data[k] = obj[k];
        }
    } else {
        this.data = JSON.parse(JSON.stringify(this.property));
    }
}

BaseEntity.prototype.set = function(k, v) {
    this.data[k] = v;
}

BaseEntity.prototype.get = function(k) {
    if (k) {
        return this.data[k];
    } else {
        return this.data;
    }
}

BaseEntity.prototype.getProperty = function() {
    var re = {}
    for (var k in this.property) {
        re[k] = this.data[k];
    }
    return re;
}

BaseEntity.prototype.doCreate = function(by) {
    this.data.created_by = by;
    this.data.created_date = Date.now();
    this.data.last_modified_by = by;
    this.data.last_modified_date = Date.now();
    this.computeIndex();
    return this;
}

BaseEntity.prototype.doModify = function(by) {
    this.data.last_modified_by = by;
    this.data.last_modified_date = Date.now();
    this.computeIndex();
    return this;
}

BaseEntity.prototype.computeIndex = function() {
}
