import VueCookies from 'vue-cookies';

export function getCookie(cookieName) {
    return VueCookies.get(cookieName);
}

export function getCookies(...cookieNames) {
    const cookies = {};
    cookieNames.forEach(name => {
        cookies[name] = VueCookies.get(name);
    });
    return cookies;
}

export function setCookie(cookieName, value) {
    VueCookies.set(cookieName, value, { secure: true, httpOnly: true, path: '/' });
}

export function setCookies(cookies) {
    Object.entries(cookies).forEach(([name, value]) => {
        VueCookies.set(name, value, { secure: true, httpOnly: true, path: '/' });
    });
}

export function removeCookie(cookieName) {
    VueCookies.remove(cookieName);
}

export function removeCookies(...cookieNames) {
    cookieNames.forEach(name => {
        VueCookies.remove(name);
    })
}