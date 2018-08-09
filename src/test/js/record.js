const faces = {
    "7": 0,
    "8": 1,
    "9": 2,
    "D": 3,
    "K": 4,
    "Z": 5,
    "A": 6,
    "B": 7
};

const suits = { "K" : 0, "P" : 1, "H" : 2, "KA" : 3 };

const card = s => (suits[s.substr(0, s.length - 1).toUpperCase()] * 8 + faces[s.charAt(s.length-1).toUpperCase()]) + 1;
const array = [
    card("p9"),
    card("ka8"),
    card("pk"),
    card("kad"),
    card("kaa"),
    card("kaz"),
    card("p8"),
    card("hk"),
    card("k8"),
    card("kz"),
    card("k7"),
    card("kab"),
    card("kk"),
    card("pz"),
    card("pa"),
    card("h7"),
    card("ka7"),
    card("pd"),
    card("hd"),
    card("ka9"),
    card("kb"),
    card("hz"),
    card("h9"),
    card("k9"),
    card("h8"),
    card("kak"),
    card("pb"),
    card("hb"),
    card("kd"),
    card("p7"),
    card("ka"),
    card("ha"),
]

console.log(array);
