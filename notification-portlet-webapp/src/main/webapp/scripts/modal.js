Vue.component("modal", {
  template: "#modal-template",
  props: {
    id: String
  }
})

// start app
new Vue({
  el: "#app",
  data: {
    items: [{ show: false }],
    id: "",
    lastActive: null,
    bodyOverflow: null
  },
  methods: {
    gotoNext: function(index) {
      this.items[index].show = false

      const modal = document.getElementById(this.id)
      let doc = modal && modal.ownerDocument
      doc.removeEventListener("focus", this.focusListener, true)
      doc.removeEventListener("keyup", this.keyUpListener)

      if (this.items[index + 1] !== undefined) {
        this.items[index + 1].show = true
        this.id = "notification" + index + 2
        let that = this
        setTimeout(function() {
          that.ada(index + 1)
        }, 250)
      } else {
        if (this.lastActive) {
          this.lastActive.focus()
        }

        let body = document.getElementsByTagName("body")[0]
        for (let x = 0; x < body.childElementCount; x++) {
          body.children[x].removeAttribute("aria-hidden")
        }
        body.style.overflow = this.bodyOverflow
      }
    },
    submit: function(notificationId, actionId, index) {
      const formData = {
        notificationId: notificationId,
        actionId: actionId
      }
      const formBody = Object.keys(formData)
        .map(function(key) {
          return (
            encodeURIComponent(key) + "=" + encodeURIComponent(formData[key])
          )
        })
        .join("&")
      this.$http
        .post(invokeActionUrl, formBody, {
          credentials: "include",
          headers: { "Content-Type": "application/x-www-form-urlencoded" }
        })
        .then(
          function(response) {
            this.gotoNext(index)
          },
          function(response) {
            console.log("Problem posting request")
          }
        )
    },
    ada: function(index) {
      const modal = document.getElementById(this.id)
      let doc = modal && modal.ownerDocument
      if (doc !== null && doc !== undefined) {
        doc.index = index
        doc.addEventListener("focus", this.focusListener, true)
        doc.addEventListener("keyup", this.keyUpListener)
      }
    },
    focusListener: function() {
      const modal = document.getElementById(this.id)
      const doc = modal && modal.ownerDocument

      const active = doc.activeElement
      const content = modal && modal.lastChild

      if (content && content !== active && !content.contains(active)) {
        content.focus()
      }
    },
    keyUpListener: function(e) {
      if (!e.keyCode || e.keyCode === 27) {
        this.gotoNext(e.currentTarget.index)
      }
    }
  },
  mounted: function() {
    this.$nextTick(function() {
      let that = this
      let body = document.getElementsByTagName("body")[0]

      this.lastActive = document.activeElement
      this.bodyOverflow = body.style.overflow

      this.$http.post(invokeNotificationServiceUrl)
      this.$http.post(getNotifications).then(function(response) {
        this.items.pop()
        for (let i = 0; i < response.data.feed.length; i++) {
          let item = response.data.feed[i]
          item.show = false
          this.items.push(item)
        }
        if (this.items.length >= 1) {
          this.id = "notification1"
          this.items[0].show = true
          setTimeout(function() {
            that.ada(0)
            for (let x = 0; x < body.childElementCount; x++) {
              body.children[x].setAttribute("aria-hidden", "true")
            }
            body.style.overflow = "hidden"
            document.getElementById("notification1").focus()
          }, 250)
        }
      })
    })
  }
})
