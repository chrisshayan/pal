function BaseEntity(property, obj) {
    this.property = property || {};
    this.property.created_by = this.property.created_by || "";
    this.property.created_date = this.property.created_date || 0;
    this.property.last_modified_by = this.property.last_modified_by || "";
    this.property.last_modified_date = this.property.last_modified_date || 0;

    if (obj) {
        if (typeof(obj.key) == "function" && typeof(obj.val) == "function") { //snapshot
            this.data = JSON.parse(JSON.stringify(this.property));
            var val = obj.val();
            for (var k in val) {
                this.data[k] = val[k];
            }
            this.data["$id"] = obj.key();
        } else {
            this.data = JSON.parse(JSON.stringify(this.property));
            for (var k in obj) {
                this.data[k] = obj[k];
            }
        }
    } else {
        this.data = JSON.parse(JSON.stringify(this.property));
    }
}

BaseEntity.prototype.set = function(k, v) {
    this.data[k] = v;
    return this;
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

BaseEntity.prototype.doCreateOrModify = function(by) {
    if (!this.data.created_by) {
        this.doCreate(by);
    } else {
        this.doModify(by);
    }
    this.computeIndex();
    return this;
}

BaseEntity.prototype.computeIndex = function() {
    return this;
}

BaseEntity.prototype.push = function(k, v) {
    this.data[k] = this.data[k] || [];
    this.data[k].push(v)
    return this;
}
